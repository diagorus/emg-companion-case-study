import ComposeApp
import SwiftUI
import UIKit

struct ContentView: View {
    var body: some View {
        ComposeViewController()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(uiColor: AppColors.background).ignoresSafeArea())
            .ignoresSafeArea(.all, edges: .bottom)
    }
}

private struct ComposeViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = MainViewControllerKt.MainViewController()
        viewController.view.backgroundColor = AppColors.background
        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    @available(iOS 16.0, *)
    func sizeThatFits(
        _ proposal: ProposedViewSize,
        uiViewController: UIViewController,
        context: Context,
    ) -> CGSize? {
        uiViewController.view.sizeThatFits(
            CGSize(
                width: proposal.width ?? UIView.noIntrinsicMetric,
                height: proposal.height ?? UIView.noIntrinsicMetric,
            ),
        )
    }
}

private enum AppColors {
    static let background = UIColor(red: 9.0 / 255.0, green: 20.0 / 255.0, blue: 25.0 / 255.0, alpha: 1)
}
