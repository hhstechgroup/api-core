package gov.ca.cwds.data.persistence.cms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.services.ServiceException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command line tool to call the <strong>Java</strong> CWDS key generation class,
 * {@link CmsKeyIdGenerator}.
 * 
 * <h3>Command line arguments:</h3>
 * <h4>Compose/generate a key:</h4> <blockquote>
 * 
 * <pre>
 * {@code java -cp bin gov.ca.cwds.rest.util.jni.JavaKeyCmdLine 0x5 "2017-06-30T04:13:51.720Z"}.
 * </pre>
 * 
 * </blockquote>
 * 
 * @author CWDS API Team
 */
public final class JavaKeyCmdLine {

  private static final Logger LOGGER = LoggerFactory.getLogger(JavaKeyCmdLine.class);

  private static final class RipCKey implements ApiMarker {

    private final String key;
    private final String staffId;
    private final Date dateIso8601;
    private final String ui19Digit;

    // key, staff id, Timestamp (hr.min.sec.1/100 sec), UI Timestamp, UI 19-digit
    public RipCKey(String line) {
      final String[] tokens = line.split("\t");
      this.key = tokens[0];
      this.staffId = tokens[1];
      this.dateIso8601 = DomainChef.uncookTimestampString(tokens[2]);
      this.ui19Digit = tokens[3];
    }

    public String getKey() {
      return key;
    }

    public String getStaffId() {
      return staffId;
    }

    public Date getDateIso8601() {
      return dateIso8601;
    }

    public String getUi19Digit() {
      return ui19Digit;
    }

    public String regenerate() {
      try {
        return generateKey(staffId, dateIso8601);
      } catch (Exception e) {
        throw new ServiceException("Oops!", e);
      }
    }

  }

  private void massTest(String fileNm) throws IOException {
    final Path pathIn = Paths.get(fileNm);

    try (Stream<String> lines = Files.lines(pathIn)) {
      lines.map(RipCKey::new).forEach(RipCKey::regenerate);
    } finally {
      // Close stream.
    }
  }

  protected static String generateKey(String staffId, Date ts) throws IOException {
    final String key = CmsKeyIdGenerator.generate(staffId, ts);
    LOGGER.info("gen: staff: {}, timestamp: {}, key: {}", staffId, ts, key);
    return key;
  }

  protected static String generateKey(String staffId, String strTs) {
    final Date ts = DomainChef.uncookISO8601Timestamp(strTs);
    return CmsKeyIdGenerator.generate(staffId, ts);
  }

  /**
   * Main method. See class javadoc for details.
   * 
   * @param args command line
   */
  public static void main(String[] args) {
    JavaKeyCmdLine run = new JavaKeyCmdLine();
    try {
      final OptionParser parser = new OptionParser("f:s:t:T::");
      final OptionSet options = parser.parse(args);

      final String staffId = options.has("s") ? (String) options.valueOf("s") : "0x5";
      final Date ts = options.has("t")
          ? DomainChef.uncookISO8601Timestamp((String) options.valueOf("t")) : new Date();
      final String fileNm = options.has("f") ? (String) options.valueOf("f") : null;

      if (StringUtils.isNotBlank(fileNm)) {
        run.massTest(fileNm);
      } else {
        run.generateKey(staffId, ts);
      }
    } catch (Exception e) {
      LOGGER.error("OOPS!", e);
    }
  }

}