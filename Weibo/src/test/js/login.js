var page = require('webpage').create(),
	system = require('system');

page.settings.userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36";

var weibo_username = system.args[1];
var weibo_password = system.args[2];

var start_url = "https://api.weibo.com/oauth2"
