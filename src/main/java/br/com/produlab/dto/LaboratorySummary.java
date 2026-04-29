package br.com.produlab.dto;

import br.com.produlab.entity.Laboratory;

import javax.persistence.Column;

public class LaboratorySummary {
    private Laboratory laboratory;
    private Integer hospitalizationValue;
    private Integer ambulatoryValue;
    private Integer emergencyValue;

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    public Integer getHospitalizationValue() {
        return hospitalizationValue;
    }

    public void setHospitalizationValue(Integer hospitalizationValue) {
        this.hospitalizationValue = hospitalizationValue;
    }

    public Integer getAmbulatoryValue() {
        return ambulatoryValue;
    }

    public void setAmbulatoryValue(Integer ambulatoryValue) {
        this.ambulatoryValue = ambulatoryValue;
    }

    public Integer getEmergencyValue() {
        return emergencyValue;
    }

    public void setEmergencyValue(Integer emergencyValue) {
        this.emergencyValue = emergencyValue;
    }
}
