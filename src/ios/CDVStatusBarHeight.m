#import "CDVStatusBarHeight.h"

@implementation CDVStatusBarHeight

- (void) height:(CDVInvokedUrlCommand*)command
{
    double statusBarHeight = [UIApplication sharedApplication].statusBarFrame.size.height;
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDouble:statusBarHeight];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

@end
