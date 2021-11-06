package com.flytekart.models.response;

import com.flytekart.models.Organisation;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrganisationResponse {

    @SerializedName("body")
    @Expose
    List<Organisation> organisations;

    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }
}
