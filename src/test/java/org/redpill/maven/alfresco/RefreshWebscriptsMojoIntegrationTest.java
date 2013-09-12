package org.redpill.maven.alfresco;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class RefreshWebscriptsMojoIntegrationTest {

  @Test
  public void testExecuteAlfresco() {
    RefreshWebscriptsMojo mojo = new RefreshWebscriptsMojo();

    try {
      mojo.setUrl(new URL("http://localhost:8080/alfresco/service/index"));

      mojo.execute();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }

  @Test
  public void testExecuteShare() throws MojoExecutionException, MalformedURLException {
    RefreshWebscriptsMojo mojo = new RefreshWebscriptsMojo();

    try {
      mojo.setUrl(new URL("http://localhost:8081/share/page/index"));

      mojo.execute();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
