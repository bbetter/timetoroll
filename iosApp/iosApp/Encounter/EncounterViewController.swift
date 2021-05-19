//
//  CreateEncounterViewController.swift
//  iosApp
//
//  Created by Andrii Puhach on 10.02.2021.
//

import UIKit
import shared

class EncounterViewController: UIViewController,UITableViewDataSource, UITableViewDelegate {
  
    private struct Constants{
        static let ROW_MARGIN = CGFloat(10)
    }
    
    private let viewModel = EncounterViewModel()
    
    @IBOutlet weak var nameTextField: UITextField!
    
    @IBOutlet weak var dexterityField: UITextField!
    
    @IBOutlet weak var initiativeTextField: UITextField!
    
    @IBOutlet weak var participantsTable: UITableView!
    
    @IBOutlet weak var actionItem: UIBarButtonItem!
    
    var code: String = ""
    
    let spinner = UIActivityIndicatorView(style: .large)
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupParticipantsTable()
        setupSubscriptions()
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return Int(viewModel.dataCount())
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
        let participant = viewModel.dataAt(index: Int32(index))
        
        bindCell(cell: cell, participant: participant, index: index)
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "encounter_segue"){
            
            if let controller = segue.destination as? EncounterSessionViewController{
                controller.code = code
            }
        }
    }
    @IBAction func onAddTouch(_ sender: UIButton) {
        let name = nameTextField.text ?? ""
        let ini = Int32(initiativeTextField.text ?? "") ?? 0
        let dex = Int32(dexterityField.text ?? "") ?? 0
        
        viewModel.addParticipant(name: name, ini: ini, dex: dex)
    
        nameTextField.text = ""
        initiativeTextField.text = ""
        dexterityField.text = ""
    }
    
    @IBAction func onActionTouch(_ sender: UIBarButtonItem) {
        viewModel.createEncounter().watch { result in
            if result != nil {
                switch result! {
                case is CreateEncounterResult.Loading:
                    self.showSpinner(onView: self.view)
                case let error as CreateEncounterResult.Error:
                    self.removeSpinner()
                    self.showErrorDialog(msg: error.message)
                case let succRes as CreateEncounterResult.Success:
                    self.removeSpinner()
                    self.code = succRes.code
                    self.performSegue(withIdentifier: "encounter_segue", sender: self)
                default:
                    NSLog("%s", "Unknown error ")
                }
            }

        }
    }
    
    @IBAction func onDiceTouch(_ sender: Any) {
        let initiative = viewModel.rollInitiative()
        initiativeTextField.text = "\(initiative)"
    }
    
    private func setupSubscriptions(){
        self.viewModel.data.watch { [weak self] _ in
            guard let self = self else { return }
            
            self.participantsTable.reloadData()
        }
    }
    
    private func setupParticipantsTable(){
        self.participantsTable.delegate = self
        self.participantsTable.dataSource = self
        
        let nib = UINib(nibName: "ParticipantCell", bundle: nil)
        self.participantsTable.register(nib, forCellReuseIdentifier: "participant_cell")
    }
    
    private func bindCell(cell: ParticipantCell, participant: Participant, index: Int){
        cell.allowDelete = true
        cell.nameLabel.text = participant.name
        cell.initiativeLabel.text = "\(participant.initiative).\(participant.dexterity)"
        
        cell.deleteButton.tag = index
        
        cell.onDeleteRowClick = { [weak self] index in
            guard let self = self else { return }
            
            self.viewModel.removeParticiapant(participant: participant)
        }
    }
    
    private func showErrorDialog(msg: String){
        let alert = UIAlertController(title: "Error", message: msg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true)
    }
}
