package se.alipsa.ride.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.alipsa.ride.environment.connections.ConnectionInfo;

public class RQueryBuilder {

  private static Logger log = LoggerFactory.getLogger(RQueryBuilder.class);
  public static final String DRIVER_VAR_NAME = "RQueryBuilderDrv";
  public static final String CONNECTION_VAR_NAME = "RQueryBuilderCon";


  public static StringBuilder baseRQueryString(ConnectionInfo con, String command, String sql) {
    StringBuilder str = new StringBuilder();

    String url = con.getUrl();
    String user = con.getUser() == null ? "" : con.getUser().trim();
    String userString;
    if (!StringUtils.isBlank(user) && !con.urlContainsLogin()) {
      userString = ", user = '" + user + "'";
    } else {
      userString = "";
      if (!con.urlContainsLogin()) {
        url = url + ";user=NA";
      }
    }
    String password = con.getPassword() == null ? "" : con.getPassword().trim();
    String passwordString;
    if (!"".equals(password) && !con.urlContainsLogin()) {
      passwordString = ", password = '" + password + "'";
    } else {
      passwordString = "";
      if (!con.urlContainsLogin()) {
        url = url + ";password=NA";
      }
    }
    str.append("library('DBI')\nlibrary('se.alipsa:R2JDBC')\n")
        .append(DRIVER_VAR_NAME).append(" <- JDBC('").append(con.getDriver()).append("')\n")
        .append(CONNECTION_VAR_NAME).append(" <- dbConnect(RQueryBuilderDrv, url = '").append(url).append("'")
        .append(userString)
        .append(passwordString)
        .append(")\n")
        .append(command).append("(").append(CONNECTION_VAR_NAME).append(", \"").append(sql).append("\")");
    //log.info(str.toString());
    return str;
  }

  public static StringBuilder cleanupRQueryString() {
    StringBuilder str = new StringBuilder();
    str.append("dbDisconnect(").append(CONNECTION_VAR_NAME).append("); rm(").append(DRIVER_VAR_NAME)
        .append("); rm(").append(CONNECTION_VAR_NAME).append(");");
    return str;
  }
}
