// SPDX-FileCopyrightText: 2026 The EDDIE Developers <eddie.developers@fh-hagenberg.at>
// SPDX-License-Identifier: Apache-2.0

package energy.eddie.aiida.adapters.datasource.openhab;

import energy.eddie.aiida.adapters.datasource.MqttDataSourceAdapter;
import energy.eddie.aiida.config.MqttConfiguration;
import energy.eddie.aiida.models.datasource.mqtt.openhab.OpenhabDataSource;
import energy.eddie.api.agnostic.aiida.ObisCode;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Adapter for an openHAB data source. The openHAB instance publishes the state of an item on the topic of this
 * data source, ending with the OBIS code the state belongs to. The state must be a number, optionally followed
 * by a unit (e.g. "1.21 kW").
 */
public class OpenhabAdapter extends MqttDataSourceAdapter<OpenhabDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenhabAdapter.class);
    private static final String TOPIC_LEVEL_SEPARATOR = "/";

    /**
     * Creates the datasource for the openHAB Adapter. It connects to the specified MQTT broker and expects that
     * openHAB publishes the states of its items on the topic of this data source.
     *
     * @param dataSource The entity of the data source.
     */
    public OpenhabAdapter(OpenhabDataSource dataSource, MqttConfiguration mqttConfiguration) {
        super(dataSource, LOGGER, mqttConfiguration);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        LOGGER.trace("Topic {} new message: {}", topic, message);

        var payload = new String(message.getPayload(), StandardCharsets.UTF_8).trim();
        var topicSuffix = topic.substring(topic.lastIndexOf(TOPIC_LEVEL_SEPARATOR) + 1);

        var obisCode = ObisCode.forCode(topicSuffix);
        if (obisCode == ObisCode.UNKNOWN) {
            LOGGER.warn("Ignoring message on topic {} because it does not end with a known OBIS code", topic);
            return;
        }

        var measurement = OpenhabMeasurement.of(obisCode, payload);
        if (measurement.isEmpty()) {
            LOGGER.debug("Ignoring payload {} on topic {} because it is not a number, optionally followed by a unit",
                         payload,
                         topic);
            return;
        }

        emitAiidaRecord(List.of(measurement.get().toAiidaRecordValue()));
    }
}
