'use strict';

let path = require('path');
let fs = require('fs');

let shx = require('shelljs');

let puglinDir = '';
let androidDir = '';

function installUploadHandler() {
    let copyFile = path.join(puglinDir, 'src/android/UploadHandler.java');
    let saveFile = path.join(androidDir, 'CordovaLib/src/org/apache/cordova/engine/UploadHandler.java');
    shx.cp(copyFile, saveFile);

    let editFile = path.join(androidDir, 'CordovaLib/src/org/apache/cordova/engine/SystemWebChromeClient.java');
    let editCode = fs.readFileSync(editFile).toString();
    editCode = editCode.replace('fileChooserParams.createIntent()', 'UploadHandler.createIntent(fileChooserParams)');
    editCode = editCode.replace('WebChromeClient.FileChooserParams.parseResult(', 'UploadHandler.parseResult(');
    fs.writeFileSync(editFile, editCode);
}

module.exports = function (context) {
    puglinDir = context.opts.plugin.dir;
    androidDir = path.join(context.opts.projectRoot, 'platforms', 'android');

    if (fs.existsSync(androidDir) && context.opts.plugin.platform == 'android') {
        installUploadHandler();
    }
};
