'use strict';

let fs = require('fs');
let path = require('path');
let shx = require('shelljs');

let puglinDir = '';
let androidDir = '';

function setupMyWebChromeClient() {
    let copyFile = path.join(puglinDir, 'src/android/MyWebChromeClient.java');
    let saveFile = path.join(androidDir, 'CordovaLib/src/org/apache/cordova/engine/MyWebChromeClient.java');
    shx.cp(copyFile, saveFile);

    let editFile = path.join(androidDir, 'CordovaLib/src/org/apache/cordova/engine/SystemWebView.java');
    let editCode = fs.readFileSync(editFile).toString();

    editCode = editCode.replace('SystemWebChromeClient', 'MyWebChromeClient');
    fs.writeFileSync(editFile, editCode);
}

module.exports = function (context) {
    puglinDir = context.opts.plugin.dir;
    androidDir = path.join(context.opts.projectRoot, 'platforms', 'android');

    if (context.opts.plugin.platform == 'android' && fs.existsSync(androidDir)) {
        setupMyWebChromeClient();
    }
};
