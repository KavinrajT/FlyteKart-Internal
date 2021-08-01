package com.flytekart.models;

public class AttributeValueDTO {
    private String variantAttributeValueId;
    private String attributeValueId;
    private String attributeName;
    private String attributeValueName;

    public String getVariantAttributeValueId() {
        return variantAttributeValueId;
    }

    public void setVariantAttributeValueId(String variantAttributeValueId) {
        this.variantAttributeValueId = variantAttributeValueId;
    }

    public String getAttributeValueId() {
        return attributeValueId;
    }

    public void setAttributeValueId(String attributeValueId) {
        this.attributeValueId = attributeValueId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValueName() {
        return attributeValueName;
    }

    public void setAttributeValueName(String attributeValueName) {
        this.attributeValueName = attributeValueName;
    }
}
