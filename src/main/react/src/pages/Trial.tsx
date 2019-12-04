import React, {Component} from "react";
import Gamepad, {Button as GPButton} from "react-gamepad";
import {connect} from "react-redux";
import {RouteComponentProps} from "react-router";
import {AppState} from "../store";
import {fetchTrial, selectNextIntensity, selectNextObservation, selectPreviousIntensity, selectPreviousObservation} from "../store/trial/operations";
import {Trial as TrialData} from "../store/trial/types";
import "./Trial.css";
import Observation from "./trial/Observation";

interface TrialProps extends RouteComponentProps<{ name: string }> {
    fetchTrial: (name: string) => void;
    selectPreviousIntensity: (name: string) => void;
    selectNextIntensity: (name: string) => void;
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
        this.gamepad(<main onKeyPress={(event) => this.trialKeyPress(event)}><div className="flex-group">
                {this.props.trial.observations.map((observation, idx) => {
                    const selected = this.props.trial.selectedObservation === idx;
                    return <div key={idx}>
                        <Observation scale={observation.scale} selected={selected}
                                     selectedIntensity={observation.selectedIntensity}
                                     trialName={this.trialName()}/>
                    </div>;
                })}
        </div></main>)

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

    // a little hack so you don't need a gamepad. But, you need to select/click on a button first. And the button focus is lost when
    // you move to a different observation.
    private trialKeyPress(event: React.KeyboardEvent<HTMLElement>) {
        const key = event.key.toLocaleUpperCase();
        if (key === "W") {
            this.props.selectPreviousIntensity(this.trialName());
        } else if (key === "S")  {
            this.props.selectNextIntensity(this.trialName());
        } else if (key === "A")  {
            this.props.selectPreviousObservation(this.trialName());
        } else if (key === "D")  {
            this.props.selectNextObservation(this.trialName());
        }
    }
}

const mapStateToProps = (state: AppState) => ({
    trial: state.trial,
});

export default connect(
    mapStateToProps,
    {fetchTrial, selectPreviousObservation, selectNextObservation, selectPreviousIntensity, selectNextIntensity},
)(Trial);
