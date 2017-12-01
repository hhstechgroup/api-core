package gov.ca.cwds.cms.data.access.service.impl;

import com.google.inject.Inject;
import gov.ca.cwds.cms.data.access.CWSIdentifier;
import gov.ca.cwds.cms.data.access.Constants.PhoneticSearchTables;
import gov.ca.cwds.cms.data.access.dao.BackgroundCheckDao;
import gov.ca.cwds.cms.data.access.dao.CountyOwnershipDao;
import gov.ca.cwds.cms.data.access.dao.EmergencyContactDetailDao;
import gov.ca.cwds.cms.data.access.dao.ExternalInterfaceDao;
import gov.ca.cwds.cms.data.access.dao.PlacementHomeDao;
import gov.ca.cwds.cms.data.access.dao.PlacementHomeProfileDao;
import gov.ca.cwds.cms.data.access.dao.PlacementHomeUcDao;
import gov.ca.cwds.cms.data.access.dao.SsaName3Dao;
import gov.ca.cwds.cms.data.access.mapper.BackgroundCheckMapper;
import gov.ca.cwds.cms.data.access.mapper.CountyOwnershipMapper;
import gov.ca.cwds.cms.data.access.mapper.EmergencyContactDetailMapper;
import gov.ca.cwds.cms.data.access.mapper.ExternalInterfaceMapper;
import gov.ca.cwds.cms.data.access.parameter.PlacementHomeParameterObject;
import gov.ca.cwds.cms.data.access.service.PlacementHomeService;
import gov.ca.cwds.cms.data.access.utils.IdGenerator;
import gov.ca.cwds.data.legacy.cms.dao.SsaName3ParameterObject;
import gov.ca.cwds.data.legacy.cms.entity.BackgroundCheck;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnership;
import gov.ca.cwds.data.legacy.cms.entity.EmergencyContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterface;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeProfile;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeUc;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import gov.ca.cwds.drools.DroolsConfiguration;
import gov.ca.cwds.drools.DroolsService;
import gov.ca.cwds.rest.exception.BusinessValidationException;
import gov.ca.cwds.rest.exception.IssueDetails;
import org.apache.commons.lang3.StringUtils;

/**
 * @author CWDS CALS API Team
 */

public class PlacementHomeServiceImpl implements PlacementHomeService {
  private String RULES_SESSION = "placement-home-session";
  private String RULES_AGENDA = "placement-home-agenda";

  @Inject
  protected DroolsService droolsService;

  @Inject
  private PlacementHomeDao placementHomeDao;

  @Inject
  private PlacementHomeUcDao placementHomeUcDao;

  @Inject
  private CountyOwnershipMapper countyOwnershipMapper;

  @Inject
  private CountyOwnershipDao countyOwnershipDao;

  @Inject
  private ExternalInterfaceDao externalInterfaceDao;

  @Inject
  private ExternalInterfaceMapper externalInterfaceMapper;

  @Inject
  private EmergencyContactDetailMapper emergencyContactDetailMapper;

  @Inject
  private EmergencyContactDetailDao emergencyContactDetailDao;

  @Inject
  private BackgroundCheckMapper backgroundCheckMapper;

  @Inject
  private BackgroundCheckDao backgroundCheckDao;

  @Inject
  private PlacementHomeProfileDao placementHomeProfileDao;

  @Inject
  private SsaName3Dao ssaName3Dao;

  @Override
  public void runBusinessValidation(PlacementHome entity) {
    DroolsConfiguration configuration = new DroolsConfiguration<PlacementHome>() {
      @Override
      public String getAgendaGroup() {
        return RULES_AGENDA;
      }

      @Override
      public String getDroolsSessionName() {
        return RULES_SESSION;
      }
    };
    Set<IssueDetails> detailsList = droolsService.validate(entity, configuration);
    if (!detailsList.isEmpty()) {
      throw new BusinessValidationException(detailsList);
    }
  }

  @Override
  public PlacementHome create(PlacementHome placementHome, PlacementHomeParameterObject parameterObject) {
    runBusinessValidation(placementHome);
    PlacementHome storedPlacementHome = placementHomeDao.create(placementHome);
    storePlacementHomeUc(storedPlacementHome, parameterObject.getStaffPersonId());
    storeCountyOwnership(storedPlacementHome.getIdentifier());
    storeExternalInterface();
    storeEmergencyContactDetail(storedPlacementHome.getIdentifier(), parameterObject.getStaffPersonId());
    storeBackgroundCheck(parameterObject.getStaffPersonId());
    storePlacementHomeProfile(parameterObject, storedPlacementHome.getIdentifier());
    prepareAddressPhoneticSearchKeywords(storedPlacementHome);
    return storedPlacementHome;
  }

  private void storeCountyOwnership(String placementHomeId) {
    CountyOwnership countyOwnership =
        countyOwnershipMapper.toCountyOwnership(placementHomeId,
            "P", Collections.emptyList());
    countyOwnershipDao.create(countyOwnership);
  }

  private void storePlacementHomeUc(PlacementHome placementHome,
      String staffPersonId) {
    PlacementHomeUc placementHomeUc = new PlacementHomeUc();
    placementHomeUc.setCityNm(StringUtils.upperCase(placementHome.getCityNm()));
    placementHomeUc.setGeoRgntcd(StringUtils.upperCase(placementHome.getGeoRgntcd()));
    placementHomeUc.setLaVndrId(StringUtils.upperCase(placementHome.getLaVndrId()));
    placementHomeUc.setLicenseNo(StringUtils.upperCase(placementHome.getLicenseNo()));
    placementHomeUc.setFacltyNm(StringUtils.upperCase(placementHome.getFacltyNm()));
    placementHomeUc.setStreetNo(StringUtils.upperCase(placementHome.getStreetNo()));
    placementHomeUc.setStreetNm(StringUtils.upperCase(placementHome.getStreetNm()));
    placementHomeUc.setPkplcHmt(placementHome.getIdentifier());
    placementHomeUc.setLstUpdId(staffPersonId);
    placementHomeUc.setLstUpdTs(LocalDateTime.now());
    placementHomeUcDao.create(placementHomeUc);
  }

  private void storeExternalInterface() {
    ExternalInterface externalInterface = externalInterfaceMapper.toExternalInterface("");
    externalInterfaceDao.create(externalInterface);
  }

  private void storeEmergencyContactDetail(String placementHomeId, String staffPersonId) {
    EmergencyContactDetail emergencyContactDetail = emergencyContactDetailMapper
            .toEmergencyContactDetail(placementHomeId, staffPersonId);
    emergencyContactDetailDao.create(emergencyContactDetail);
  }

  private void storeBackgroundCheck(String staffPersonId) {
    BackgroundCheck backgroundCheck = backgroundCheckMapper.toBackgroundCheck("", staffPersonId);
    backgroundCheckDao.create(backgroundCheck);
  }

  private void storePlacementHomeProfile(PlacementHomeParameterObject parameterObject,
      String placementHomeId) {
    for (CWSIdentifier homeLanguage : parameterObject.getHomeLanguages()) {
      PlacementHomeProfile placementHomeProfile = new PlacementHomeProfile();
      placementHomeProfile.setThirdId(IdGenerator.generateId(parameterObject.getStaffPersonId()));
      placementHomeProfile.setChrctrC((short) homeLanguage.getCwsId());
      placementHomeProfile.setChrctrCd("L");
      placementHomeProfile.setLstUpdId(parameterObject.getStaffPersonId());
      placementHomeProfile.setLstUpdTs(LocalDateTime.now());
      placementHomeProfile.setFkplcHmT(placementHomeId);
      placementHomeProfileDao.create(placementHomeProfile);
    }
  }

  private void prepareAddressPhoneticSearchKeywords(PlacementHome placementHome) {
    SsaName3ParameterObject parameterObject = new SsaName3ParameterObject();
    parameterObject.setTableName(PhoneticSearchTables.ADR_PHTT);
    parameterObject.setCrudOper("I");
    parameterObject.setIdentifier(placementHome.getIdentifier());
    parameterObject.setNameCd("P");
    parameterObject.setStreetNumber(placementHome.getStreetNo());
    parameterObject.setStreetName(placementHome.getStreetNm());
    parameterObject.setGvrEntc(placementHome.getGvrEntc());
    parameterObject.setUpdateTimeStamp(new Date());
    parameterObject.setUpdateId(placementHome.getLastUpdateId());
    ssaName3Dao.callStoredProc(parameterObject);
  }

  void setDroolsService(DroolsService droolsService) {
    this.droolsService = droolsService;
  }

}
