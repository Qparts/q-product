package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleCriteria {
    private int criteriaId;
    private String criteriaDescription;
    private String criteriaAbbrDescription;
    private String criteriaUnitDescription;
    private String criteriaType;
    private String rawValue;
    private String formattedValue;
    private boolean immediateDisplay;
    private boolean isMandatory;
    private boolean isInterval;

    public int getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(int criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getCriteriaDescription() {
        return criteriaDescription;
    }

    public void setCriteriaDescription(String criteriaDescription) {
        this.criteriaDescription = criteriaDescription;
    }

    public String getCriteriaAbbrDescription() {
        return criteriaAbbrDescription;
    }

    public void setCriteriaAbbrDescription(String criteriaAbbrDescription) {
        this.criteriaAbbrDescription = criteriaAbbrDescription;
    }

    public String getCriteriaUnitDescription() {
        return criteriaUnitDescription;
    }

    public void setCriteriaUnitDescription(String criteriaUnitDescription) {
        this.criteriaUnitDescription = criteriaUnitDescription;
    }

    public String getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    public boolean isImmediateDisplay() {
        return immediateDisplay;
    }

    public void setImmediateDisplay(boolean immediateDisplay) {
        this.immediateDisplay = immediateDisplay;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isInterval() {
        return isInterval;
    }

    public void setInterval(boolean interval) {
        isInterval = interval;
    }
}
