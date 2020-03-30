//
//  Trends.swift
//  CoronaTrace
//
//  Created by Sai Gurrapu on 3/29/20.
//  Copyright © 2020 Sai Gurrapu. All rights reserved.
//

import SwiftUI

struct Trends: View {
    var body: some View {
        Image("trends")
        .resizable()
            .resizable()
            .aspectRatio(contentMode: .fill)
    }
}

struct Trends_Previews: PreviewProvider {
    static var previews: some View {
        Trends()
    }
}
