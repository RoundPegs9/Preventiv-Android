//
//  Home.swift
//  CoronaTrace
//
//  Created by Sai Gurrapu on 3/29/20.
//  Copyright © 2020 Sai Gurrapu. All rights reserved.
//

import SwiftUI

struct Home: View {
    var body: some View {
        Image("home")
        .resizable()
            .resizable()
            .aspectRatio(contentMode: .fill)
            .padding(.top, -15)
    }
}

struct Home_Previews: PreviewProvider {
    static var previews: some View {
        Home()
    }
}
