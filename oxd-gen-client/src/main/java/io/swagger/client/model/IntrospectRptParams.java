/*
 * oxd-server
 * oxd-server
 *
 * OpenAPI spec version: 4.0.beta
 * Contact: yuriyz@gluu.org
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * IntrospectRptParams
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-07-23T08:29:06.952Z")
public class IntrospectRptParams {
  @SerializedName("oxd_id")
  private String oxdId = null;

  @SerializedName("rpt")
  private String rpt = null;

  public IntrospectRptParams oxdId(String oxdId) {
    this.oxdId = oxdId;
    return this;
  }

   /**
   * Get oxdId
   * @return oxdId
  **/
  @ApiModelProperty(example = "bcad760f-91ba-46e1-a020-05e4281d91b6", required = true, value = "")
  public String getOxdId() {
    return oxdId;
  }

  public void setOxdId(String oxdId) {
    this.oxdId = oxdId;
  }

  public IntrospectRptParams rpt(String rpt) {
    this.rpt = rpt;
    return this;
  }

   /**
   * Get rpt
   * @return rpt
  **/
  @ApiModelProperty(required = true, value = "")
  public String getRpt() {
    return rpt;
  }

  public void setRpt(String rpt) {
    this.rpt = rpt;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IntrospectRptParams introspectRptParams = (IntrospectRptParams) o;
    return Objects.equals(this.oxdId, introspectRptParams.oxdId) &&
        Objects.equals(this.rpt, introspectRptParams.rpt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oxdId, rpt);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IntrospectRptParams {\n");
    
    sb.append("    oxdId: ").append(toIndentedString(oxdId)).append("\n");
    sb.append("    rpt: ").append(toIndentedString(rpt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

