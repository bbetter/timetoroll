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
    @IBOutlet weak var dexLabel: UILabel!
    @IBOutlet weak var deleteButton: UIButton!
    @IBOutlet weak var activeIndicatorView: UIView!
    
    @IBAction func onDeleteTouch(_ sender: UIButton) {
        onDeleteRowClick(sender.tag)
    }
    
    var onDeleteRowClick: (Int) -> Void = { _ in }
    
    func configureWith(participant p: Participant, allowDelete: Bool, isSelected: Bool){
        activeIndicatorView.isHidden = !isSelected
        deleteButton.isHidden = !allowDelete
        
        nameLabel.text = p.name
        initiativeLabel.text = "\(p.initiative)"
        dexLabel.text = format(dex: p.dexterity)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        activeIndicatorView.clipsToBounds = true
        
        cardView.layer.cornerRadius = 4
        cardView.layer.masksToBounds = true
        cardView.layer.shadowColor = UIColor.lightGray.cgColor
        cardView.layer.shadowOffset = CGSize(width: 1, height: 1);
        cardView.layer.shadowOpacity = 0.6
        cardView.layer.borderWidth = 0.7
        cardView.layer.borderColor =  UIColor.lightGray.cgColor
    }
    
    func format(dex: Int32) -> String{
        switch dex {
        case _ where dex > 0:
            return "+\(dex)"
        case _ where dex < 0:
            return "\(dex)"
        default:
           return "0"
        }
    }
}
