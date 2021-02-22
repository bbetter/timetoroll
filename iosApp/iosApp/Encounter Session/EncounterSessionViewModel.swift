//
//  EncounterSessionViewModel.swift
//  iosApp
//
//  Created by Andrii Puhach on 08.02.2021.
//

import Foundation
import shared

class EncounterSessionViewModel: NativeViewModel{
    
    private let tracker: RemoteEncounterTracker
    
    init(tracker: RemoteEncounterTracker){
        self.tracker = tracker
    }
    
    func listenToServer() {
        launchInScope {
            tracker.start()
            
        }
    }
}
