package utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Constants;

public class LoginUtils {

	public static void invalidateCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {

		} else {
			for (Cookie cookie : cookies) {
				if (Constants.COOKIE_NAME.equals(cookie.getName())) {
					cookie.setMaxAge(0);
				}
			}
		}
	}
	
	public static String createCookie(HttpServletResponse response, String id) {
		Cookie cookie = new Cookie(Constants.COOKIE_NAME, id);
		// 60 days
		cookie.setMaxAge(60 * 60 * 24 * 60);
		response.addCookie(cookie);
		return id;
	}
}
