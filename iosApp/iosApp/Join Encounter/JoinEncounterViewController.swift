//
//  JoinEncounterViewController.swift
//  iosApp
//
//  Created by Andrii Puhach on 08.02.2021.
//

import UIKit
import shared

class JoinEncounterViewController: UIViewController, UITableViewDataSource, UITableViewDelegate{
    
    private struct Constants{
        static let ROW_MARGIN = CGFloat(10)
    }
    
    @IBOutlet weak var participantsTable: UITableView!
    
    @IBOutlet weak var codeTextField: UITextField!
    
    @IBOutlet weak var nameTextField: UITextField!
    
    @IBOutlet weak var initiativeTextField: UITextField!
    
    @IBOutlet weak var dexTextField: UITextField!
    
    private let viewModel = EncounterJoinViewModel()
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupParticipantsTable()
        setupSubscriptions()
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return Int(self.viewModel.dataCount())
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let headerview =  UIView(
            frame: CGRect(
                x: 0,
                y: 0,
                width: tableView.bounds.size.width,
                height: Constants.ROW_MARGIN
            )
        )
        return headerview
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return Constants.ROW_MARGIN
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "participant_cell", for: indexPath) as! ParticipantCell
        
        let index = indexPath.section
        
        let participant = self.viewModel.dataAt(index: Int32(index))
        
        bindCell(cell: cell, participant: participant, index: index)
        
        return cell
    }
    
    @IBAction func onJoinTouch(_ sender: UIBarButtonItem) {
        
        guard let code = self.codeTextField.text else { return }
        
        LiveFlowKt.asLiveFlow(
            viewModel.join(code: code),
            scope: viewModel.scope
        ).watch { result in
            if result != nil {
                switch result! {
                case is JoinEncounterResult.Loading:
                    self.showSpinner(onView: self.view)
                case let error as JoinEncounterResult.Error:
                    self.removeSpinner()
                    self.showErrorDialog(msg: error.message)
                case is CreateEncounterResult.Success:
                    self.removeSpinner()
                    self.performSegue(withIdentifier: "join_encounter_segue", sender: self)
                default:
                    NSLog("%s", "Unknown error ")
                }
            }

        }
    }
    
    @IBAction func onAddTouch(_ sender: Any) {
        let name = nameTextField.text ?? ""
        let initiative = Int32(initiativeTextField.text ?? "") ?? 0
        let dexterity = Int32(dexTextField.text ?? "") ?? 0
        
        self.viewModel.addParticipant(name: name, ini: initiative, dex: dexterity)
        
        nameTextField.text = ""
        initiativeTextField.text = ""
        dexTextField.text = ""
    }
    
    @IBAction func onRollTouch(_ sender: Any) {
        let initiative = viewModel.rollInitiative()
        initiativeTextField.text = "\(initiative)"
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "join_encounter_segue"){
            if let controller = segue.destination as? EncounterSessionViewController {
                guard let code = self.codeTextField.text else { return }
                controller.code = code
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool){
        super.viewWillDisappear(animated)
        self.viewModel.onCleared()
    }
    
    private func showErrorDialog(msg: String){
        let alert = UIAlertController(title: "Error", message: msg, preferredStyle: .alert)

        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))

        self.present(alert, animated: true)
    }
    
    private func setupParticipantsTable(){
        participantsTable.delegate = self
        participantsTable.dataSource = self
    
        let nib = UINib(nibName: "ParticipantCell", bundle: nil)
        participantsTable.register(nib, forCellReuseIdentifier: "participant_cell")
    }
    
    private func bindCell(cell: ParticipantCell, participant: Participant, index: Int){
        cell.allowDelete = true
        cell.nameLabel.text = participant.name
        cell.initiativeLabel.text = "\(participant.initiative).\(participant.dexterity)"
        
        cell.deleteButton.tag = index
        cell.onDeleteRowClick = { [weak self] index in
            guard let self = self else { return }
            self.viewModel.removeParticipant(participant: participant)
        }
    }
    
    private func setupSubscriptions(){
        viewModel.data.watch { [weak self] _ in
            guard let self = self else { return }
            self.participantsTable.reloadData()
        }
    }
}
