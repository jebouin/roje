package roje.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animations {
	public static Timeline getDiscreteRotation(Node node, int duration, int steps) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		final int frameTime = duration / steps;
		for (int i = 0; i < steps; i++) {
			final KeyValue kv = new KeyValue(node.rotateProperty(), i * 360 / 8);
			final KeyFrame kf = new KeyFrame(Duration.millis(frameTime * i), kv);
			final KeyFrame kf2 = new KeyFrame(Duration.millis(frameTime * (i + 1)), kv);
			timeline.getKeyFrames().add(kf);
			timeline.getKeyFrames().add(kf2);
		}
		return timeline;
	}
}
