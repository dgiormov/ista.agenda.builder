package utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginUtils {

	public static void invalidateCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {

		} else {
			for (Cookie cookie : cookies) {
				if (Constants.COOKIE_PROVIDER_KEY.equals(cookie.getName())) {
					cookie.setMaxAge(0);
				}
			}
		}
	}
	
	public static void createCookie(HttpServletResponse response, String provider) {
		Cookie cookie = new Cookie(Constants.COOKIE_PROVIDER_KEY, provider);
		// 60 days
		cookie.setMaxAge(60 * 60 * 24 * 60);
		response.addCookie(cookie);
	}
	
	public static String findCookieValue(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
