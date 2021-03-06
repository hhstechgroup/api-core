package gov.ca.cwds.authorizer.drools;

import static gov.ca.cwds.authorizer.ClientCondition.DIFFERENT_COUNTY_SEALED;
import static gov.ca.cwds.authorizer.ClientCondition.DIFFERENT_COUNTY_SENSITIVE;
import static gov.ca.cwds.authorizer.ClientCondition.NO_CONDITIONS;
import static gov.ca.cwds.authorizer.ClientCondition.NO_COUNTY_SEALED;
import static gov.ca.cwds.authorizer.ClientCondition.NO_COUNTY_SENSITIVE;
import static gov.ca.cwds.authorizer.ClientCondition.SAME_COUNTY_SEALED;
import static gov.ca.cwds.authorizer.ClientCondition.SAME_COUNTY_SENSITIVE;
import static gov.ca.cwds.authorizer.StaffPrivilegeType.COUNTY_SEALED;
import static gov.ca.cwds.authorizer.StaffPrivilegeType.COUNTY_SENSITIVE;
import static gov.ca.cwds.authorizer.StaffPrivilegeType.SOCIAL_WORKER_ONLY;
import static gov.ca.cwds.authorizer.StaffPrivilegeType.STATE_SEALED;
import static gov.ca.cwds.authorizer.StaffPrivilegeType.STATE_SENSITIVE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.authorizer.ClientCondition;
import gov.ca.cwds.authorizer.StaffPrivilegeType;
import gov.ca.cwds.authorizer.drools.configuration.ClientAbstractAuthorizationDroolsConfiguration;
import gov.ca.cwds.drools.DroolsService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 * This is to test "client-authorization-rules/client-authorization.drl" drools
 *
 * @author CWDS TPT-3 Team
 */
public class ClientAuthorizationDrlTest {
  private final DroolsService droolsService = new DroolsService();
  private final DroolsAuthorizationService droolsAuthorizationService
      = new DroolsAuthorizationService(droolsService);

  @Test
  public void authorizeClientRead_success_whenNoConditionsClient() {
    assertClientAccessMatrixValue(NO_CONDITIONS, list(SOCIAL_WORKER_ONLY), true);
    assertClientAccessMatrixValue(NO_CONDITIONS, list(COUNTY_SENSITIVE), true);
    assertClientAccessMatrixValue(NO_CONDITIONS, list(COUNTY_SEALED), true);
    assertClientAccessMatrixValue(NO_CONDITIONS, list(STATE_SENSITIVE), true);
    assertClientAccessMatrixValue(NO_CONDITIONS, list(STATE_SEALED), true);
  }

  @Test
  public void authorizeClientRead_success_whenSameCountySensitiveClient() {
    assertClientAccessMatrixValue(SAME_COUNTY_SENSITIVE, list(SOCIAL_WORKER_ONLY), false);
    assertClientAccessMatrixValue(SAME_COUNTY_SENSITIVE, list(COUNTY_SENSITIVE), true);
    assertClientAccessMatrixValue(SAME_COUNTY_SENSITIVE, list(COUNTY_SEALED), false);
    assertClientAccessMatrixValue(SAME_COUNTY_SENSITIVE, list(STATE_SENSITIVE), true);
    assertClientAccessMatrixValue(SAME_COUNTY_SENSITIVE, list(STATE_SEALED), false);
  }

  @Test
  public void authorizeClientRead_success_whenSameCountySealedClient() {
    assertClientAccessMatrixValue(SAME_COUNTY_SEALED, list(SOCIAL_WORKER_ONLY), false);
    assertClientAccessMatrixValue(SAME_COUNTY_SEALED, list(COUNTY_SENSITIVE), false);
    assertClientAccessMatrixValue(SAME_COUNTY_SEALED, list(COUNTY_SEALED), true);
    assertClientAccessMatrixValue(SAME_COUNTY_SEALED, list(STATE_SENSITIVE), false);
    assertClientAccessMatrixValue(SAME_COUNTY_SEALED, list(STATE_SEALED), true);
  }

  @Test
  public void authorizeClientRead_success_whenDifferentCountySensitiveClient() {
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SENSITIVE, list(SOCIAL_WORKER_ONLY), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SENSITIVE, list(COUNTY_SENSITIVE), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SENSITIVE, list(COUNTY_SEALED), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SENSITIVE, list(STATE_SENSITIVE), true);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SENSITIVE, list(STATE_SEALED), false);
  }

  @Test
  public void authorizeClientRead_success_whenDifferentCountySealedClient() {
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, list(SOCIAL_WORKER_ONLY), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, list(COUNTY_SENSITIVE), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, list(COUNTY_SEALED), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, list(STATE_SENSITIVE), false);
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, list(STATE_SEALED), false);
  }

  @Test
  public void authorizeClientRead_success_whenNoCountySensitiveClient() {
    assertClientAccessMatrixValue(NO_COUNTY_SENSITIVE, list(SOCIAL_WORKER_ONLY), false);
    assertClientAccessMatrixValue(NO_COUNTY_SENSITIVE, list(COUNTY_SENSITIVE), true);
    assertClientAccessMatrixValue(NO_COUNTY_SENSITIVE, list(COUNTY_SEALED), false);
    assertClientAccessMatrixValue(NO_COUNTY_SENSITIVE, list(STATE_SENSITIVE), false);
    assertClientAccessMatrixValue(NO_COUNTY_SENSITIVE, list(STATE_SEALED), false);
  }

  @Test
  public void authorizeClientRead_success_whenNoCountySealedClient() {
    assertClientAccessMatrixValue(NO_COUNTY_SEALED, list(SOCIAL_WORKER_ONLY), false);
    assertClientAccessMatrixValue(NO_COUNTY_SEALED, list(COUNTY_SENSITIVE), false);
    assertClientAccessMatrixValue(NO_COUNTY_SEALED, list(COUNTY_SEALED), true);
    assertClientAccessMatrixValue(NO_COUNTY_SEALED, list(STATE_SENSITIVE), false);
    assertClientAccessMatrixValue(NO_COUNTY_SEALED, list(STATE_SEALED), true);
  }

  @Test
  public void authorizeClientRead_true_whenMultiplePrivilegesWithSomePermitted() {
    final List<StaffPrivilegeType> privileges = Arrays.asList(
        SOCIAL_WORKER_ONLY,
        COUNTY_SENSITIVE,
        COUNTY_SEALED,
        STATE_SENSITIVE,
        STATE_SEALED
    );
    assertClientAccessMatrixValue(NO_COUNTY_SEALED, privileges, true);
  }

  @Test
  public void authorizeClientRead_false_whenMultiplePrivilegesWithNoPermitted() {
    final List<StaffPrivilegeType> privileges = Arrays.asList(
        SOCIAL_WORKER_ONLY,
        COUNTY_SENSITIVE,
        COUNTY_SEALED,
        STATE_SENSITIVE,
        STATE_SEALED
    );
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, privileges, false);
  }

  @Test
  public void authorizeClientRead_false_whenNoPrivileges() {
    assertClientAccessMatrixValue(DIFFERENT_COUNTY_SEALED, emptyList(), false);
  }

  private void assertClientAccessMatrixValue(ClientCondition condition,
      List<StaffPrivilegeType> privileges, boolean expectedResult) {
    ClientAbstractAuthorizationDroolsConfiguration droolsConfiguration = new ClientAbstractAuthorizationDroolsConfiguration();
    List instances = new ArrayList<>();
    instances.add(condition);
    final boolean actualResult = droolsAuthorizationService.authorizeObjectOperation(privileges, droolsConfiguration, instances);
    assertThat(actualResult, is(expectedResult));
  }

  private List<StaffPrivilegeType> list(StaffPrivilegeType privilegeType) {
    return singletonList(privilegeType);
  }
}