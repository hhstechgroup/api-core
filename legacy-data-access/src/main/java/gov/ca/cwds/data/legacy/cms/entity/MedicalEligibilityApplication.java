package gov.ca.cwds.data.legacy.cms.entity;

import gov.ca.cwds.data.legacy.cms.CmsPersistentObject;
import gov.ca.cwds.data.legacy.cms.entity.enums.IveEligebleStatus;
import gov.ca.cwds.data.legacy.cms.entity.enums.TypeOfApplication;
import org.hibernate.annotations.*;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@NamedQuery(
        name = MedicalEligibilityApplication.NQ_FIND_BY_CHILD_CLIENT_ID,
        query =
                "SELECT mea FROM gov.ca.cwds.data.legacy.cms.entity.MedicalEligibilityApplication mea"
                        + " WHERE mea.childClient.victimClientId = :" + MedicalEligibilityApplication.NQ_PARAM_CHILD_CLIENT_ID
)
/**
 * @author CWDS CM API Team
 * <p>
 * This records information provided on a MEDICAL  ELIGIBILITY APPLICATION (MC-250) for a CHILD  CLIENT.
 */
@Entity
@Table(name = "MEDELIGT")
@SuppressWarnings({"squid:S3437"}) //LocalDate is serializable
public class MedicalEligibilityApplication extends CmsPersistentObject {
    public static final String NQ_FIND_BY_CHILD_CLIENT_ID =
            "gov.ca.cwds.data.legacy.cms.entity.MedicalEligibilityApplication.findByChildClientId";
    public static final String NQ_PARAM_CHILD_CLIENT_ID = "childId";

    private static final long serialVersionUID = -8983343570652292014L;


    /**
     * ID - A sequencing number used to uniquely identify each MEDICAL_ELIGIBILITY_APPLICATION.
     * This ID is composed of a base 62 Creation Timestamp and the STAFF PERSON ID (a sequential
     * 3 digit base 62 number generated by the system). This value eliminates the need for an additional
     * set of Creation Timestamp and Creation User ID which is needed to satisfy the Audit Trail requirement.
     */
    @Id
    @Column(name = "IDENTIFIER", nullable = false, length = 10)
    private String identifier;


    /**
     * ADOPTION_AGREEMENT_TERM_DATE - The date when the CHILD CLIENT's adoption  agreement terminates or renews.
     */
    @Column(name = "ADDOC_ENDT", nullable = true)
    private LocalDate adoptionAgreementTermDate;


    /**
     * CA6_ATTACHED_IND  - This indicates whether a declaration of valid alien status has been submitted with the MC-250 application (Y) or not (N).
     * CA6 has been renamed to MC13.
     */
    @Column(name = "W_CA6_IND", nullable = false, length = 1)
    @Type(type = "yes_no")
    private boolean ca6AttachedInd;


    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "FKCHLD_CLT", referencedColumnName = "FKCLIENT_T")
    private ChildClient childClient;


    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "FKSTFPERST", referencedColumnName = "IDENTIFIER")
    private StaffPerson staffPerson;


    /**
     * IV_E_ELIGIBLE - This indicates whether the CHILD_CLIENT currently is eligible for Federal AFDC-FC benefits,
     * or the social worker has reason to believe the CHILD_CLIENT will be eligible for Federal AFDC-FC benefits (Y),
     * or not (N). If the social worker cannot determine this information at the time, the unknown (U) value should be used.
     */
    @Column(name = "IVE_ELG_CD", nullable = false, length = 1)
    @Convert(converter = IveEligebleStatus.IveEligebleStatusConverter.class)
    private IveEligebleStatus iveEligebleStatus;


    /**
     * MED_ELIGIBILITY_APPLICATION_DOC - This is the ID from the DRMS_DOCUMENT entity type which identifies a unique
     * Medi-Cal Eligibility Application Document. The associated document, created by a social worker, includes a child's
     * background information, amount of money being paid from public funds for the child's care, information on the responsible
     * public agency and the contact person for the medical coverage of that child. The document's purpose is to apply for
     * Medi-Cal coverage for a child who is not living with a parent and for whom public funds are being provided.
     */
    @Column(name = "MEDELG_DOC", length = 10)
    private String medEligibilityApplicationDoc;


    /**
     * RETROACTIVE_MONTHS_COUNT - The number of retroactive months for which  eligibility is requested.
     */
    @Column(name = "RETR_MNCNT", nullable = false)
    private short retroactiveMonthsCount;

    /**
     * SIGNED_DATE - The date that the MC-250 was signed by the social worker.
     */
    @Column(name = "SIGNED_DT")
    private LocalDate signedDate;


    /**
     * SOCIAL_SECURITY_CLAIM_NUMBER - If the CHILD CLIENT is receiving social security  benefits (due to death of a parent, etc.),
     * this  number represents the social security number  that the CHILD CLIENT is claiming against.  The  Social Security Claim
     * Number includes a 'C'  (Child), a sequential number for each child  claiming against that SSN, and the SSN of the  Parent.
     */
    @Column(name = "SSN_CLM_NO", length = 11)
    private String ssnClaimNumber;

    /**
     * SUBMISSION_DATE - The date that the MC-250 was submitted to the  eligibility worker.
     */
    @Column(name = "SUBMSN_DT")
    private LocalDate submissionDate;

    /**
     * TYPE_OF_APPLICATION - This indicates whether the application is a new application, redetermination, or retroactive application.
     * Valid values are NW = New, RD = Redetermination, RT = Retroactive.
     */
    @Column(name = "APPLCTN_CD", nullable = false, length = 2)
    @Convert(converter = TypeOfApplication.TypeOfApplicationConverter.class)
    private TypeOfApplication typeOfApplication;


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getAdoptionAgreementTermDate() {
        return adoptionAgreementTermDate;
    }

    public void setAdoptionAgreementTermDate(LocalDate adoptionAgreementTermDate) {
        this.adoptionAgreementTermDate = adoptionAgreementTermDate;
    }

    public boolean isCa6AttachedInd() {
        return ca6AttachedInd;
    }

    public void setCa6AttachedInd(boolean ca6AttachedInd) {
        this.ca6AttachedInd = ca6AttachedInd;
    }

    public ChildClient getChildClient() {
        return childClient;
    }

    public void setChildClient(ChildClient childClient) {
        this.childClient = childClient;
    }

    public StaffPerson getStaffPerson() {
        return staffPerson;
    }

    public void setStaffPerson(StaffPerson staffPerson) {
        this.staffPerson = staffPerson;
    }

    public IveEligebleStatus getIveEligebleStatus() {
        return iveEligebleStatus;
    }

    public void setIveEligebleStatus(IveEligebleStatus iveEligebleStatus) {
        this.iveEligebleStatus = iveEligebleStatus;
    }

    public String getMedEligibilityApplicationDoc() {
        return medEligibilityApplicationDoc;
    }

    public void setMedEligibilityApplicationDoc(String medEligibilityApplicationDoc) {
        this.medEligibilityApplicationDoc = medEligibilityApplicationDoc;
    }

    public short getRetroactiveMonthsCount() {
        return retroactiveMonthsCount;
    }

    public void setRetroactiveMonthsCount(short retroactiveMonthsCount) {
        this.retroactiveMonthsCount = retroactiveMonthsCount;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }

    public String getSsnClaimNumber() {
        return ssnClaimNumber;
    }

    public void setSsnClaimNumber(String ssnClaimNumber) {
        this.ssnClaimNumber = ssnClaimNumber;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public TypeOfApplication getTypeOfApplication() {
        return typeOfApplication;
    }

    public void setTypeOfApplication(TypeOfApplication typeOfApplication) {
        this.typeOfApplication = typeOfApplication;
    }

    @Override
    public Serializable getPrimaryKey() {
        return getIdentifier();
    }
}
