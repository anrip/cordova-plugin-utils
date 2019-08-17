var exec = require('cordova/exec');

module.exports = function (onSuccess, onError) {
    exec(onSuccess, onError, 'StatusBarHeight', 'height', []);
};
