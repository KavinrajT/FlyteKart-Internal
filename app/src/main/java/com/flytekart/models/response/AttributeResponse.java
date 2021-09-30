package com.flytekart.models.response;

import com.flytekart.models.Attribute;
import com.flytekart.models.AttributeValue;
import com.flytekart.models.Organisation;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttributeResponse {

    @SerializedName("attribute")
    @Expose
    private Attribute attribute;

    @SerializedName("attributeValues")
    @Expose
    private List<AttributeValue> attributeValues;

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public List<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<AttributeValue> attributeValues) {
        this.attributeValues = attributeValues;
    }
}
