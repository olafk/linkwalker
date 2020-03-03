# Linkwalker Dynamic Include

This plugin will add some JS+CSS to each page that requests 
links with a HEAD request and marks them pink and -striked through-
if the link appears invalid.

It's based on Jan Verweij's suggestion, slightly adapted and turned
into a deployable plugin through Liferay's Dynamic Include mechanism. 

## How to build

This plugin was built in a Liferay Workspace's `modules` folder for 
Liferay DXP 7.2 

## Limitations

Due to occasional CORS failures, links to external sites might generate
false positives, e.g. they might show as invalid even though they are
valid. This can be mitigated by specifically checking for relative links
or known hostnames - if it becomes a problem in any system, feel free to
send pullrequests with appropriate workarounds.