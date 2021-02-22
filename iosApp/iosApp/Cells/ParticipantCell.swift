//
//  EditableParticipantCell.swift
//  iosApp
//
//  Created by Andrii Puhach on 14.02.2021.
//

import UIKit
import shared

class ParticipantCell: UITableViewCell {
    
    enum Constants{
        static let BOTTOM_SPACE = CGFloat(10.0)
    }
    
    @IBOutlet weak var deleteConstraint: NSLayoutConstraint!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var initiativeLabel: UILabel!
    @IBOutlet weak var deleteButton: UIButton!
    
    @IBAction func onDeleteTouch(_ sender: UIButton) {
        onRowClick(sender.tag)
    }
    
    var allowDelete: Bool = false{
        didSet{
            deleteButton.isHidden = !allowDelete
            if(allowDelete){
                deleteConstraint.constant = 33
            }
            else{
                deleteConstraint.constant = 0
            }
        }
    }
    
    var onRowClick: (Int) -> Void = { _ in }
    
}
