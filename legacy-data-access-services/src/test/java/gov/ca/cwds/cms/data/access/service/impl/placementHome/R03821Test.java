package gov.ca.cwds.cms.data.access.service.impl.placementHome;

import gov.ca.cwds.data.legacy.cms.entity.OtherAdultsInPlacementHome;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

/**
 * Created by TPT2 on 12/21/2017.
 */
public class R03821Test extends BaseDocToolRulesPlacementHomeTest {

  private static final String RULE_NAME = "R-03821";

  @Test
  public void checkValidStartAndBirthDate()  throws Exception {
    OtherAdultsInPlacementHome otherAdultsInPlacementHome = new OtherAdultsInPlacementHome();
    otherAdultsInPlacementHome.setStartDt(LocalDate.now());
    otherAdultsInPlacementHome.setBirthDt(LocalDate.now().minus(1, ChronoUnit.DAYS));
    placementHomeEntityAwareDTO.getEntity().setOtherAdultsInPlacementHomes(Collections.singletonList(otherAdultsInPlacementHome));
    checkRuleValid(placementHomeEntityAwareDTO, RULE_NAME);
  }

  @Test
  public void checkInvalidStartAndBirthDate() throws Exception {
    OtherAdultsInPlacementHome otherAdultsInPlacementHome = new OtherAdultsInPlacementHome();
    otherAdultsInPlacementHome.setStartDt(LocalDate.now());
    otherAdultsInPlacementHome.setBirthDt(LocalDate.now().plus(1, ChronoUnit.DAYS));
    placementHomeEntityAwareDTO.getEntity().setOtherAdultsInPlacementHomes(Collections.singletonList(otherAdultsInPlacementHome));
    checkRuleViolatedOnce(placementHomeEntityAwareDTO, RULE_NAME);
  }


}
