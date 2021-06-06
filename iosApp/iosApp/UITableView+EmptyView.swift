//
//  UITableViewController + EmptyView.swift
//  TTR
//
//  Created by Andrii Puhach on 23.05.2021.
//

import UIKit

extension UITableView {
    func setNoDataPlaceholder(_ message: String) {
        let frame = CGRect(
            x: self.center.x,
            y: self.center.y,
            width: self.bounds.size.width,
            height: self.bounds.size.height
        )
        let label = UILabel(frame: frame)
        label.textAlignment = .center
        label.textColor = UIColor.init(rgb: 0x673AB7)
        label.text = message
        // styling
        label.sizeToFit()
        
        self.isScrollEnabled = false
        self.backgroundView = label
    }
    
    func removeNoDataPlaceholder() {
        self.isScrollEnabled = true
        self.backgroundView = nil
    }
}
