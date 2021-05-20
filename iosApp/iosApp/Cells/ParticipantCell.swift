//
//  EditableParticipantCell.swift
//  iosApp
//
//  Created by Andrii Puhach on 14.02.2021.
//

import UIKit
import shared

class ParticipantCell: UITableViewCell {
    private var shadowLayer: CAShapeLayer!
    
    @IBOutlet weak var cardView: UIView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var initiativeLabel: UILabel!
    @IBOutlet weak var deleteButton: UIButton!
    
    @IBAction func onDeleteTouch(_ sender: UIButton) {
        onDeleteRowClick(sender.tag)
    }
    
    var allowDelete: Bool = false{
        didSet{
            deleteButton.isHidden = !allowDelete
        }
    }
    
    var onDeleteRowClick: (Int) -> Void = { _ in }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        // just use the layer's shadow... adding the Bezier
        //let shadowPath = UIBezierPath(roundedRect: innerView.bounds, cornerRadius: cornerRadius)
        //innerView.layer.shadowPath = shadowPath.cgPath
        
        cardView.layer.cornerRadius = 6
        cardView.layer.masksToBounds = false
        cardView.layer.shadowColor = UIColor.lightGray.cgColor
        cardView.layer.shadowOffset = CGSize(width: 1, height: 1);
        cardView.layer.shadowOpacity = 0.7
        cardView.layer.borderWidth = 1.0
        cardView.layer.borderColor =  UIColor.lightGray.cgColor
    }
}
