package org.redpill.maven.alfresco;

import java.io.Closeable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which refreshes the web scripts via a HTTP POST to a specified URL.
 */
@Mojo(name = "refresh", defaultPhase = LifecyclePhase.PACKAGE)
public class RefreshWebscriptsMojo extends AbstractMojo {

  public static final String DEFAULT_USERNAME = "admin";
  public static final String DEFAULT_PASSWORD = "admin";
  public static final String DEFAULT_METHOD = "post";

  /**
   * The URL to send the POST to.
   */
  @Parameter(property = "url", required = true, alias = "url")
  private URL _url;

  /**
   * The username to authenticate the call for.
   */
  @Parameter(property = "username", defaultValue = DEFAULT_USERNAME, alias = "username")
  private String _username = DEFAULT_USERNAME;

  /**
   * The password to authenticate the call for.
   */
  @Parameter(property = "password", defaultValue = DEFAULT_PASSWORD, alias = "password")
  private String _password = DEFAULT_PASSWORD;

  /**
   * The method used for refresh, different for share and repo?
   */
  @Parameter(property = "method", defaultValue = "post", alias = "method")
  private String _method = DEFAULT_METHOD;

  @Override
  public void execute() throws MojoExecutionException {
    // first check if a URL is supplied, if not just exit
    if (_url == null) {
      getLog().debug("The URL is empty, so no refresh can take place.");
      return;
    }

    // then do a ping to see if the server is up, if not, log and just exit
    if (!ping()) {
      getLog().info("Can't contact " + _url.getHost() + " on port " + _url.getPort() + ", exiting...");
      return;
    }

    HttpHost targetHost = new HttpHost(_url.getHost(), _url.getPort(), "http");

    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(_username, _password));

    CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

    CloseableHttpResponse response = null;

    try {
      // Create AuthCache instance
      AuthCache authCache = new BasicAuthCache();

      // Generate BASIC scheme object and add it to the local auth cache
      BasicScheme basicAuth = new BasicScheme();
      authCache.put(targetHost, basicAuth);

      // Add AuthCache to the execution context
      HttpClientContext localContext = HttpClientContext.create();
      localContext.setAuthCache(authCache);

      if (_method.equalsIgnoreCase("post")) {
        HttpPost httppost = new HttpPost(_url.toURI());

        response = client.execute(httppost);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("reset", "on"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httppost.setEntity(entity);
        httppost.setHeader("Accept-Charset", "iso-8859-1,utf-8");
        httppost.setHeader("Accept-Language", "en-us");

        response = client.execute(httppost);
      } else if (_method.equalsIgnoreCase("get")) {
        HttpGet get = new HttpGet(_url.toExternalForm());

        get.setHeader("Accept-Charset", "iso-8859-1,utf-8");
        get.setHeader("Accept-Language", "en-us");

        response = client.execute(get);
      }

      // if no response, no method has been passed
      if (response == null) {
        return;
      }

      int statusCode = response.getStatusLine().getStatusCode();
      String reasonPhrase = response.getStatusLine().getReasonPhrase();

      getLog().info("URL '" + _url.toExternalForm() + "' called with response code: '" + statusCode + "' and reason phrase '" + reasonPhrase + "'");
    } catch (Exception ex) {
      getLog().error("Can't contact " + _url.getHost() + " on port " + _url.getPort() + ", exiting...");
    } finally {
      closeQuietly(response);
      closeQuietly(client);
    }
  }

  private boolean ping() {
    try {
      TelnetClient telnetClient = new TelnetClient();
      telnetClient.setDefaultTimeout(500);
      telnetClient.connect(_url.getHost(), _url.getPort());

      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  private void closeQuietly(Closeable closeable) {
    try {
      closeable.close();
    } catch (Exception ex) {
      // swallow any exceptions
    }
  }

  public void setUrl(URL url) {
    this._url = url;
  }

}
