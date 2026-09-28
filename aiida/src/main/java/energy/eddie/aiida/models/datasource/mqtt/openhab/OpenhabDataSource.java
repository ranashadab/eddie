// SPDX-FileCopyrightText: 2026 The EDDIE Developers <eddie.developers@fh-hagenberg.at>
// SPDX-License-Identifier: Apache-2.0

package energy.eddie.aiida.models.datasource.mqtt.openhab;

import energy.eddie.aiida.dtos.datasource.mqtt.openhab.OpenhabDataSourceDto;
import energy.eddie.aiida.models.datasource.DataSourceType;
import energy.eddie.aiida.models.datasource.mqtt.MqttDataSource;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue(DataSourceType.Identifiers.OPENHAB)
public class OpenhabDataSource extends MqttDataSource {

    @SuppressWarnings("NullAway")
    protected OpenhabDataSource() {}

    public OpenhabDataSource(OpenhabDataSourceDto dto, UUID userId) {
        super(dto, userId);
    }
}
