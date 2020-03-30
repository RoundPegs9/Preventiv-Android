//
//  ContentView.swift
//  Quarantine
//
//  Created by Sai Gurrapu on 3/19/20.
//  Copyright © 2020 Sai Gurrapu. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var bluetooth: BLE
      
    var body: some View {
         VStack {
              Button(action: {
                print("\(self.bluetooth.connected)")
              }) {
                   Text("check connection")
              }
          
         }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
