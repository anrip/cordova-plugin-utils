#import "StatusBarHeight.h"

@implementation StatusBarHeight

- (void) value:(CDVInvokedUrlCommand*)command
{
    double height = [UIApplication sharedApplication].statusBarFrame.size.height;
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDouble:height];

    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

@end
