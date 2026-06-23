import Foundation

@objc public class BlePrinter: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
