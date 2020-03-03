package de.olafkock.liferay.linkwalker;

import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * This component adds JS code to the page that checks some links for validity
 * and marks them in case they're not valid (e.g. the HEAD request returns a code
 * >399 or an error. Note that there are false positives in case the targetted
 * site does not allow access through CORS rules.
 * 
 * Based on a code suggestion by Jan Verweij, slightly edited on error handling,
 * and turned into a DynamicInclude to minimize interaction.
 * 
 * @author Olaf Kock, Jan Verweij
 */

@Component(
		immediate=true,
		service=DynamicInclude.class
)
public class LinkwalkerDynamicInclude implements DynamicInclude {

	@Override
	public void include(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String key)
			throws IOException {
		ThemeDisplay td = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		if(td != null && 
		   td.getPermissionChecker().isGroupAdmin(td.getScopeGroupId())) {
	
			PrintWriter pw = httpServletResponse.getWriter();
			pw.println("<script>function linkwalkerUrlExists(element, callback) {\n" +
					"   var href=element.attr(\"href\");\n" +
					"	if (href != \";\" && ( href.startsWith(\"http\") || href.startsWith(\"/\"))) {\n" + 
					"		var xhr = new XMLHttpRequest();\n" + 
					"		xhr.onreadystatechange = function() {\n" + 
					"			if (xhr.readyState === 4) {\n" + 
					"				callback(xhr.status < 400, 'xhr'+xhr.status);\n" + 
					"			}\n" + 
					"		};\n" + 
					"		xhr.onerror = function() {\n" + 
					"			callback(false,'xhrerror');\n" + 
					"		}\n" + 
					"		xhr.open('HEAD', element.attr(\"href\"));\n" + 
					"		xhr.send();\n" + 
					"	}\n" + 
					"}\n" + 
					"\n" + 
				// for widget pages
				// Notice that selectors must be quite specific - see LPS-104516
					"$(function() {\n" + 
					"    $( \".portlet-body a\" ).each(function( index ) {\n" + 
					"        var element = $(this);\n" + 
					"        linkwalkerUrlExists(element, function(exists, hint) {\n" + 
					"            console.log('\"%s\" exists?', element.attr(\"href\"), exists);\n" + 
					"            if (!exists) {\n" + 
					"                element.addClass(\"brokenlink\");\n" + 
					"                element.addClass(hint);\n" + 
					"            }\n" + 
					"        });\n" + 
					"    });\n" + 
					"});\n" + 	
				// for content pages
					"$(function() {\n" + 
					"    $( \"div#main-content a\" ).each(function( index ) {\n" + 
					"        var element = $(this);\n" + 
					"        linkwalkerUrlExists(element, function(exists, hint) {\n" + 
					"            console.log('\"%s\" exists?', element.attr(\"href\"), exists);\n" + 
					"            if (!exists) {\n" + 
					"                element.addClass(\"brokenlink\");\n" + 
					"                element.addClass(hint);\n" + 
					"            }\n" + 
					"        });\n" + 
					"    });\n" + 
					"});\n" + 
					"</script>");
			pw.println("<style>a.brokenlink {\n" + 
					"    background-color: lightpink;\n" + 
					"    text-decoration: line-through;\n" + 
					"}</style>");
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}
}
