//
//  ContentView.swift
//  Quarantine
//
//  Created by Sai Gurrapu on 3/19/20.
//  Copyright © 2020 Sai Gurrapu. All rights reserved.
//

import SwiftUI

struct ContentView: View {
   @State private var selection = 0
      
    var body: some View {
         TabView(selection: $selection){
             Home()
                 .tabItem {
                         Image("TabBarIcon-Cancun")
                         Text("Home")
                 }
             Trends()
              .tabItem {
                      Image("TabBarIcon-London")
                      Text("Trends")
              }
            Map()
            .tabItem {
                    Image("TabBarIcon-London")
                    Text("Map")
            }
             
                
         }//End TabView
             .font(.headline)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
