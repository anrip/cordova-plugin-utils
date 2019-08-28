'use strict';

let path = require('path');
let fs = require('fs');

let shx = require('shelljs');

let selfPath = path.dirname(__dirname);

function installUploadHandler(androidPath) {
    let copyFile = path.join(selfPath, 'src/android/UploadHandler.java');
    let saveFile = path.join(androidPath, 'CordovaLib/src/org/apache/cordova/engine/UploadHandler.java');
    shx.cp(copyFile, saveFile);

    let editFile = path.join(androidPath, 'CordovaLib/src/org/apache/cordova/engine/SystemWebChromeClient.java');
    let editCode = fs.readFileSync(editFile).toString();
    editCode = editCode.replace('fileChooserParams.createIntent()', 'UploadHandler.createIntent(fileChooserParams)');
    fs.writeFileSync(editFile, editCode);
}

module.exports = function (context) {
    let projectRoot = context.opts.projectRoot;

    let androidPath = path.join(projectRoot, 'platforms', 'android');
    if (fs.existsSync(androidPath) && context.opts.plugin.platform == 'android') {
        installUploadHandler(androidPath);
    }
};
