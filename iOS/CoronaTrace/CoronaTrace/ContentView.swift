//
//  ContentView.swift
//  CoronaTrace
//
//  Created by Sai Gurrapu on 3/29/20.
//  Copyright © 2020 Sai Gurrapu. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    @State private var selection = 0
 
    var body: some View {
        TabView(selection: $selection){
            Home()
                 .tabItem {
                         Image(systemName: "house.fill")
                         Text("Home")
                 }
             Trends()
              .tabItem {
                      Image(systemName: "chart.bar.fill")
                      Text("Trends")
              }
            Map()
            .tabItem {
                    Image(systemName: "globe")
                    Text("Live Map")
            }
        }.edgesIgnoringSafeArea(.top)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
