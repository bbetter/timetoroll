//
//  JoinEncounterViewController.swift
//  iosApp
//
//  Created by Andrii Puhach on 08.02.2021.
//

import UIKit
import MBProgressHUD
import shared

class JoinEncounterViewController: UIViewController, UITableViewDataSource, UITableViewDelegate{
   
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
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Int(self.viewModel.dataCount())
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "participant_cell", for: indexPath) as! ParticipantCell
        
        let index = indexPath.row
        
        let participant = self.viewModel.dataAt(index: Int32(index))
        cell.configureWith(participant: participant, allowDelete: true, isSelected: false)
    
        cell.deleteButton.tag = index
        
        cell.onDeleteRowClick = { [weak self] index in
            guard let self = self else { return }
            self.viewModel.removeParticipant(participant: participant)
        }
        return cell
    }
    
    @IBAction func onJoinTouch(_ sender: UIBarButtonItem) {
        
        guard let code = self.codeTextField.text else { return }
        
        viewModel.join(code: code).watch { result in
            if result != nil {
                switch result! {
                case is JoinEncounterResult.Loading:
                    MBProgressHUD.showAdded(to: self.view, animated: true)
                case let error as JoinEncounterResult.Error:
                    MBProgressHUD.hide(for: self.view, animated: true)
                    self.showErrorDialog(msg: error.message)
                case is JoinEncounterResult.Success:
                    MBProgressHUD.hide(for: self.view, animated: true)
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
    
    private func setupSubscriptions(){
        viewModel.data.watch { [weak self] _ in
            guard let self = self else { return }
            self.participantsTable.reloadData()
        }
    }
}
