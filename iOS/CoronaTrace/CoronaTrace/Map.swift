//
//  Map.swift
//  CoronaTrace
//
//  Created by Sai Gurrapu on 3/29/20.
//  Copyright © 2020 Sai Gurrapu. All rights reserved.
//

import SwiftUI

struct Map: View {
    var body: some View {
        Image("map")
        .resizable()
            .resizable()
            .aspectRatio(contentMode: .fill)
    }
}

struct Map_Previews: PreviewProvider {
    static var previews: some View {
        Map()
    }
}
