import React, {Component} from "react";
import Gamepad, {Button as GPButton} from "react-gamepad";
import {connect} from "react-redux";
import {RouteComponentProps} from "react-router";
import {AppState} from "../store";
import {fetchTrial, selectNextObservation, selectPreviousObservation} from "../store/trial/operations";
import {Trial as TrialData} from "../store/trial/types";
import "./Trial.css";
import Observation from "./trial/Observation";

interface TrialProps extends RouteComponentProps<{ name: string }> {
    fetchTrial: (name: string) => void;
    selectPreviousObservation: (name: string) => void;
    selectNextObservation: (name: string) => void;

    trial: TrialData;
}

class Trial extends Component<TrialProps> {
    constructor(props: Readonly<TrialProps>) {
        super(props);
        this.state = {
            selectedScale: 0,
        };
        this.handleButtonDown = this.handleButtonDown.bind(this);
    }

    public componentDidMount() {
        this.props.fetchTrial(this.trialName());
    }

    public render = () =>
        this.gamepad(<div className={"container"}>
            <div className={"row"}>
                {this.props.trial.observations.map((observation, idx) => {
                    const selected = this.props.trial.selectedObservation === idx;
                    return <div key={idx} className={"col"}>
                        <Observation scale={observation.scale} selected={selected}
                                     selectedIntensity={observation.selectedIntensity}
                                     trialName={this.trialName()}/>
                    </div>;
                })}
            </div>
        </div>)

    private trialName = () => this.props.match.params.name;

    private handleButtonDown(buttonName: GPButton) {
        if (buttonName === "Y") {
            this.props.selectPreviousObservation(this.trialName());
        } else if (buttonName === "X") {
            this.props.selectNextObservation(this.trialName());
        }
    }

    private gamepad = (children: JSX.Element) =>
        <Gamepad onButtonDown={this.handleButtonDown}>{children}</Gamepad>

}

const mapStateToProps = (state: AppState) => ({
    trial: state.trial,
});

export default connect(
    mapStateToProps,
    {fetchTrial, selectPreviousObservation, selectNextObservation},
)(Trial);
