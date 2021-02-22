//
//  EncounterSessionViewController.swift
//  iosApp
//
//  Created by Andrii Puhach on 07.02.2021.
//

import UIKit
import shared

class EncounterSessionViewController: UIViewController, UITableViewDataSource, UITableViewDelegate{
    
    enum Constants{
        static let ROW_MARGIN = CGFloat(10)
    }
    
    //MARK: widgets
    @IBOutlet weak var actionButton: UIButton!
    
    @IBOutlet weak var timerLabel: UILabel!
    
    @IBOutlet weak var participantsTable: UITableView!
   
    private let formatter = DateComponentsFormatter()
    
    private var encounterData: EncounterData? = nil
    
    var code: String = ""
    
    lazy var viewModel = EncounterSessionViewModel(
        code: self.code,
        onSessionDataUpdated: { [weak self] data in
            guard let self = self else { return }
            self.encounterData = data
            self.participantsTable?.reloadData()
            
            self.updateTrackerButton(data)
            self.updateTrackerTime(data)
        }
    )
    
    private func updateTrackerButton(_ data: EncounterData){
        self.actionButton.isHidden = !data.isPlayPauseAllowed
      
        let image = data.isPaused ? "play.fill" : "pause.fill"
        self.actionButton.setImage(UIImage(systemName: image), for: .normal)
    }
    
    private func updateTrackerTime(_ data: EncounterData){
        if data.tick <= 5 {
            self.timerLabel.textColor = UIColor.red
        }
        else {
            self.timerLabel.textColor = UIColor.black
        }
        
        self.timerLabel.text = self.formatter.string(from: TimeInterval.init(data.tick))
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        formatter.allowedUnits = [.minute, .second]
        formatter.zeroFormattingBehavior = .pad
        
        viewModel.listenToServer()
        
        self.setupParticipantsTable()
        
        title = "Encounter #\(code)"
    }
    
    private func setupParticipantsTable(){
        participantsTable.delegate = self
        participantsTable.dataSource = self
        let nib = UINib(nibName: "ParticipantCell", bundle: nil)
        participantsTable.register(nib, forCellReuseIdentifier: "participant_cell")
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return encounterData?.participants.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let headerview =  UIView(frame: CGRect(x: 0, y: 0, width: tableView.bounds.size.width, height: Constants.ROW_MARGIN))
        return headerview
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return Constants.ROW_MARGIN
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "participant_cell", for: indexPath) as! ParticipantCell
    
        let participantIndex = indexPath.section
        
        guard let participant = encounterData?.participants[participantIndex] else {
            return cell
        }
        
        if let turnIndex = encounterData?.turnIndex {
            if(turnIndex == participantIndex){
                cell.backgroundColor = UIColor.yellow
            } else{
                cell.backgroundColor = UIColor.systemGray
            }
        } else {
            cell.backgroundColor = UIColor.systemGray
        }
        
        cell.allowDelete = false
        cell.nameLabel?.text = participant.name
        cell.initiativeLabel?.text = "\(participant.initiative).\(participant.dexterity)"
        return cell
    }

    @IBAction func onActionTouch(_ sender: UIButton) {
        viewModel.resume()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        viewModel.onCleared()
    }

}
