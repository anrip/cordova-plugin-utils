'use strict';

let path = require('path');
let fs = require('fs');

let shx = require('shelljs');

function removeUploadHandler(androidPath) {
    let saveFile = path.join(androidPath, 'CordovaLib/src/org/apache/cordova/engine/UploadHandler.java');
    shx.rm(saveFile);

    let editFile = path.join(androidPath, 'CordovaLib/src/org/apache/cordova/engine/SystemWebChromeClient.java');
    let editCode = fs.readFileSync(editFile).toString();
    editCode = editCode.replace('UploadHandler.createIntent(fileChooserParams)', 'fileChooserParams.createIntent()');
    fs.writeFileSync(editFile, editCode);
}

module.exports = function (context) {
    let projectRoot = context.opts.projectRoot;

    let androidPath = path.join(projectRoot, 'platforms', 'android');
    if (fs.existsSync(androidPath) && context.opts.plugin.platform == 'android') {
        removeUploadHandler(androidPath);
    }
};
