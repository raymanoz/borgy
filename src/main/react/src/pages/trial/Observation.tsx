import React, {Component} from "react";
import Gamepad, {Button as GPButton} from "react-gamepad";
import {connect} from "react-redux";
import Button from "../../components/Button";
import {logEvent} from "../../store/trial/operations";
import {Intensity, Scale} from "../Scale";
import "../Trial.css";

interface Props {
    logEvent: (trialName: string, scale: Scale, intensity: Intensity) => void;

    scale: Scale;
    trialName: string;
    selectedIntensity?: number;
    selected: boolean;
}

class Observation extends Component<Props> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.handleButtonDown = this.handleButtonDown.bind(this);
    }

    public render() {
        return this.gamepad(this.buttons(this.props.scale));
    }

    private logEvent(intensity: Intensity) {
        this.props.logEvent(this.props.trialName, this.props.scale, intensity);
    }

    private buttons(scale: Scale) {
        const maybeSelected = this.props.selected ? "selected" : "";
        return <div className={"container observation " + maybeSelected}>
            <h1 className="trial">{scale.description}</h1>
            {scale.intensities.map((intensity, index) =>
                <div key={index} className="flex-group flex-start border-bottom padding-vertical-1">
                    <Button intensity={intensity.number}
                            selected={this.props.selectedIntensity != null ? this.props.selectedIntensity === index : false}/>
                    <div className={"borg-button-label"}>{intensity.label}</div>
                </div>)}
        </div>;
    }

    private handleButtonDown(buttonName: GPButton) {
        const selectedIntensity = this.props.selectedIntensity == null ? 0 :
            buttonName === "A" ? Math.max(0, this.props.selectedIntensity - 1) :
                buttonName === "B" ? Math.min(this.props.scale.intensities.length - 1, (this.props.selectedIntensity + 1)) :
                    this.props.selectedIntensity;

        if (selectedIntensity != null && this.props.selectedIntensity !== selectedIntensity && (buttonName === "A" || buttonName === "B")) {
            this.logEvent(this.props.scale.intensities[selectedIntensity]);
        }
    }

    private gamepad = (children: JSX.Element) =>
        (this.props.selected) ?
            <Gamepad onButtonDown={this.handleButtonDown}>{children}</Gamepad> :
            <div>{children}</div>
}

export default connect(
    () => ({}),
    { logEvent },
)(Observation);
