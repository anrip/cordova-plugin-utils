'use strict';

let fs = require('fs');
let path = require('path');
let shx = require('shelljs');

let puglinDir = '';
let androidDir = '';

function removeMyWebChromeClient() {
    let saveFile = path.join(androidDir, 'CordovaLib/src/org/apache/cordova/engine/MyWebChromeClient.java');
    shx.rm(saveFile);

    let editFile = path.join(androidDir, 'CordovaLib/src/org/apache/cordova/engine/SystemWebView.java');
    let editCode = fs.readFileSync(editFile).toString();

    editCode = editCode.replace(/MyWebChromeClient/g, 'SystemWebChromeClient');
    fs.writeFileSync(editFile, editCode);
}

module.exports = function (context) {
    puglinDir = context.opts.plugin.dir;
    androidDir = path.join(context.opts.projectRoot, 'platforms', 'android');

    if (context.opts.plugin.platform == 'android' && fs.existsSync(androidDir)) {
        removeMyWebChromeClient();
    }
};
