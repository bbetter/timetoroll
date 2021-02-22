//
//  DI.swift
//  iosApp
//
//  Created by Andrii Puhach on 07.02.2021.
//

import Foundation
import shared

func startKoin() {
    let koinApplication = IosKoinKt.doInit()
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin? = nil
var koin: Koin_coreKoin {
    return _koin!
}
