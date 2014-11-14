package auth.openidconnect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.OAuthProviderType;

import com.google.gson.Gson;

public final class Utils {
    
	private Utils() {
    	
    }

	public static final String PATH_TO_PROVIDERS = "/WEB-INF/classes/providers/";
	
	private static final Map<String, ProviderData> providers = new HashMap<String, ProviderData>();

    private static final String REDIRECT_URI_LOCAL = "http://localhost:8080/redirect";
    private static final String REDIRECT_URI_DEV = "https://istadevi024096trial.hanatrial.ondemand.com/redirect";
    private static final String REDIRECT_URI_PROD = "https://ista.hana.ondemand.com/redirect";

    public static final String FACEBOOK = OAuthProviderType.FACEBOOK.getProviderName();
    
    public static final String GOOGLE = OAuthProviderType.GOOGLE.getProviderName();
    public static final String TWITTER = "twitter"; 
    
    public static final String LINKEDIN = OAuthProviderType.LINKEDIN.getProviderName();

	public static final String COOKIE_PROVIDER_NAME = "login_provider";

	public static final String ACCESS_TOKEN_SESSION_KEY = "accessToken";

    public static String getClientId(String app, HttpServletRequest request){
    	ProviderData provider = getProvider(app, request);
    	String host = request.getServerName();
    	if(host.contains("localhost")){
    		return provider.getLocalClientId();	
    	} else if(host.contains("hanatrial") && host.contains("dev")){
    		return provider.getDevClientId();
    	} else if(host.contains("hana.ondemand.com")){
    		return provider.getProductiveClientId();
    	}
    	return null;
    }
    
    public static final boolean isProductiveApplication(HttpServletRequest request){
    	String host = request.getServerName();
    	if(host.contains("localhost")){
    		return false;	
    	} else if(host.contains("hanatrial") && host.contains("dev")){
    		return false;
    	} 
    	return true;
    }
    
    public static ProviderData getProvider(String name, HttpServletRequest request){
    	if(providers.get(name) != null){
    		return providers.get(name);
    	}
   		Gson g = new Gson();
   		final BufferedReader providerData = new BufferedReader(new InputStreamReader(request.getServletContext().getResourceAsStream(PATH_TO_PROVIDERS+name.toLowerCase()+".json")));

    	ProviderData pd = g.fromJson(providerData, ProviderData.class);
    	providers.put(name, pd);
    	return pd;
    }
    
//    public static ProviderData loadLinkedInKeystore(HttpServletRequest request){
//    	KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//    	InputStream instream = request.getServletContext().getResourceAsStream(PATH_TO_PROVIDERS+"linkedin.jks");
//        try {
//            trustStore.load(instream, "nopassword".toCharArray());
//        } finally {
//            instream.close();
//        }
//    }
    
    public static String getClientSecret(String app, HttpServletRequest request){
    	ProviderData provider = getProvider(app, request);
    	String host = request.getServerName();
    	if(host.contains("localhost")){
    		return provider.getLocalClientSecret();	
    	} else if(host.contains("hanatrial") && host.contains("dev")){
    		return provider.getDevClientSecret();
    	} else if(host.contains("hana.ondemand.com")){
    		return provider.getProductiveClientSecret();
    	}
    	return null;
    }

    public static void validateAuthorizationParams(OAuthParams oauthParams, String host) throws ApplicationException {

        String authzEndpoint = oauthParams.getAuthzEndpoint();
        String tokenEndpoint = oauthParams.getTokenEndpoint();
        String clientId = oauthParams.getClientId();
        String clientSecret = oauthParams.getClientSecret();
        String redirectUri = oauthParams.getRedirectUri();

        StringBuffer sb = new StringBuffer();

        if (isEmpty(authzEndpoint)) {
            sb.append("Authorization Endpoint ");
        }

        if (isEmpty(tokenEndpoint)) {
            sb.append("Token Endpoint ");
        }

        if (isEmpty(clientId)) {
            sb.append("Client ID ");
        }

        if (isEmpty(clientSecret)) {
            sb.append("Client Secret ");
        }

        if (!getRedirectUri(host).equals(redirectUri)) {
            sb.append("Redirect URI");
        }

        String incorrectParams = sb.toString();
        if ("".equals(incorrectParams)) {
            return;
        }
        throw new ApplicationException("Incorrect parameters: " + incorrectParams);

    }

    public static void validateTokenParams(OAuthParams oauthParams, String host) throws ApplicationException {

        String authzEndpoint = oauthParams.getAuthzEndpoint();
        String tokenEndpoint = oauthParams.getTokenEndpoint();
        String clientId = oauthParams.getClientId();
        String clientSecret = oauthParams.getClientSecret();
        String redirectUri = oauthParams.getRedirectUri();
        String authzCode = oauthParams.getAuthzCode();

        StringBuffer sb = new StringBuffer();

        if (isEmpty(authzCode)) {
            sb.append("Authorization Code ");
        }

        if (isEmpty(authzEndpoint)) {
            sb.append("Authorization Endpoint ");
        }

        if (isEmpty(tokenEndpoint)) {
            sb.append("Token Endpoint ");
        }

        if (isEmpty(clientId)) {
            sb.append("Client ID ");
        }

        if (isEmpty(clientSecret)) {
            sb.append("Client Secret ");
        }

        if (!getRedirectUri(host).equals(redirectUri)) {
            sb.append("Redirect URI");
        }

        String incorrectParams = sb.toString();
        if ("".equals(incorrectParams)) {
            return;
        }
        throw new ApplicationException("Incorrect parameters: " + incorrectParams);

    }

    private static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    public static String isIssued(String value) {
        if (isEmpty(value)) {
            return "(Not issued)";
        }
        return value;
    }

	public static String getRedirectUri(String host) {
		if(host.contains("localhost")){
    		return REDIRECT_URI_LOCAL;	
    	} else if(host.contains("hanatrial") && host.contains("dev")){
    		return REDIRECT_URI_DEV;
    	} else if(host.contains("hana.ondemand.com")){
    		return REDIRECT_URI_PROD;
    	}
		return null;
	}
}
