import React, {Component} from "react";
import Gamepad, {Button as GPButton} from "react-gamepad";
import {connect} from "react-redux";
import Button from "../../components/Button";
import {selectNextIntensity, selectPreviousIntensity} from "../../store/trial/operations";
import {Scale} from "../Scale";
import "../Trial.css";

interface Props {
    selectPreviousIntensity: (name: string) => void;
    selectNextIntensity: (name: string) => void;

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
        if (buttonName === "A") {
            this.props.selectPreviousIntensity(this.props.trialName);
        } else if (buttonName === "B") {
            this.props.selectNextIntensity(this.props.trialName);
        }
    }

    private gamepad = (children: JSX.Element) =>
        (this.props.selected) ?
            <Gamepad onButtonDown={this.handleButtonDown}>{children}</Gamepad> :
            <div>{children}</div>
}

export default connect(
    () => ({}),
    { selectPreviousIntensity, selectNextIntensity },
)(Observation);
