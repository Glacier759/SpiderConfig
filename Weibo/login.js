var page = require('webpage').create(),
	system = require('system');

page.settings.userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36";

page.open("http://www.weibo.com", function(status) {
	if ( status !== "success" ) {
		console.log("Unable to access network");
	}
	else {
		page.evaluate(function(text) {
			$("[name='username']").val(text);
		}, "l_ee_hom@msn.cn");
	}
	page.render('weibo.jpeg');
});

page.onResource = function(res, network) {
	if ( res.stage == "end" ) {
		page.reader('wei.jpeg');
		phantom.exit();
	}
};
