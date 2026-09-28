// SPDX-FileCopyrightText: 2026 The EDDIE Developers <eddie.developers@fh-hagenberg.at>
// SPDX-License-Identifier: Apache-2.0

package energy.eddie.aiida.adapters.datasource.openhab;

import energy.eddie.aiida.adapters.datasource.SmartMeterAdapterMeasurement;
import energy.eddie.api.agnostic.aiida.ObisCode;
import energy.eddie.api.agnostic.aiida.UnitOfMeasurement;

import java.util.Optional;

/**
 * A single measurement received from an openHAB item. The state of an openHAB item is a number, optionally
 * followed by a unit (e.g. "1.21 kW").
 */
public class OpenhabMeasurement extends SmartMeterAdapterMeasurement {
    private static final char UNIT_SEPARATOR = ' ';

    private final ObisCode obisCode;
    private final UnitOfMeasurement rawUnitOfMeasurement;

    private OpenhabMeasurement(ObisCode obisCode, String rawValue, UnitOfMeasurement rawUnitOfMeasurement) {
        super(obisCode.toString(), rawValue);
        this.obisCode = obisCode;
        this.rawUnitOfMeasurement = rawUnitOfMeasurement;
    }

    /**
     * Parses the state of an openHAB item into a measurement.
     *
     * @param obisCode The OBIS code the state belongs to.
     * @param state    The state of the openHAB item.
     * @return The measurement, or empty if the state is not a number optionally followed by a known unit.
     */
    public static Optional<OpenhabMeasurement> of(ObisCode obisCode, String state) {
        var separatorIndex = state.indexOf(UNIT_SEPARATOR);
        var rawValue = separatorIndex < 0 ? state : state.substring(0, separatorIndex).trim();
        var rawUnitOfMeasurement = separatorIndex < 0
                ? obisCode.unitOfMeasurement()
                : UnitOfMeasurement.forValue(state.substring(separatorIndex + 1).trim());

        if (rawUnitOfMeasurement == UnitOfMeasurement.UNKNOWN) {
            return Optional.empty();
        }

        try {
            Double.parseDouble(rawValue);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return Optional.of(new OpenhabMeasurement(obisCode, rawValue, rawUnitOfMeasurement));
    }

    @Override
    public ObisCode obisCode() {
        return obisCode;
    }

    @Override
    public UnitOfMeasurement rawUnitOfMeasurement() {
        return rawUnitOfMeasurement;
    }
}
