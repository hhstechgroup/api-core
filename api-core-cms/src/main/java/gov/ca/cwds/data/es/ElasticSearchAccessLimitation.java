package gov.ca.cwds.data.es;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.ca.cwds.data.std.ApiObjectIdentity;

/**
 * Access Limitation (sealed, sensitive, restricted).
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class ElasticSearchAccessLimitation extends ApiObjectIdentity {
  @JsonProperty("limited_access_code")
  private String limitedAccessCode;

  @JsonProperty("limited_access_date")
  private String limitedAccessDate;

  @JsonProperty("limited_access_description")
  private String limitedAccessDescription;

  @JsonProperty("limited_access_government_entity_id")
  private String limitedAccessGovernmentEntityId;

  @JsonProperty("limited_access_government_entity_name")
  private String limitedAccessGovernmentEntityName;

  public String getLimitedAccessCode() {
    return limitedAccessCode;
  }

  public void setLimitedAccessCode(String limitedAccessCode) {
    this.limitedAccessCode = limitedAccessCode;
  }

  public String getLimitedAccessDate() {
    return limitedAccessDate;
  }

  public void setLimitedAccessDate(String limitedAccessDate) {
    this.limitedAccessDate = limitedAccessDate;
  }

  public String getLimitedAccessDescription() {
    return limitedAccessDescription;
  }

  public void setLimitedAccessDescription(String limitedAccessDescription) {
    this.limitedAccessDescription = limitedAccessDescription;
  }

  public String getLimitedAccessGovernmentEntityId() {
    return limitedAccessGovernmentEntityId;
  }

  public void setLimitedAccessGovernmentEntityId(String limitedAccessGovernmentEntityId) {
    this.limitedAccessGovernmentEntityId = limitedAccessGovernmentEntityId;
  }

  public String getLimitedAccessGovernmentEntityName() {
    return limitedAccessGovernmentEntityName;
  }

  public void setLimitedAccessGovernmentEntityName(String limitedAccessGovernmentEntityName) {
    this.limitedAccessGovernmentEntityName = limitedAccessGovernmentEntityName;
  }

}
