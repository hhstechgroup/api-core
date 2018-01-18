package gov.ca.cwds.cms.data.access.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cms.data.access.inject.DataAccessServicesSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnership;
import org.hibernate.SessionFactory;

/**
 * @author CWDS CALS API Team
 */

public class CountyOwnershipDao extends BaseDaoImpl<CountyOwnership> {

  @Inject
  public CountyOwnershipDao(@DataAccessServicesSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}