import React, {Component, KeyboardEventHandler} from "react";
import Gamepad, {Button as GPButton} from "react-gamepad";
import {RouteComponentProps} from "react-router";
import {server} from "../utils/server";
import {Trial} from "./Trial";
import "./Trial.css";

interface UrlParams {
    name: string;
}

interface AggregateTrialProps extends RouteComponentProps<UrlParams> {
}

interface AggregateTrialState {
    scales: string[];
    selectedScale: number;
}

export class AggregateTrial extends Component<AggregateTrialProps, AggregateTrialState> {
    constructor(props: Readonly<AggregateTrialProps>) {
        super(props);
        this.state = {
            scales: [],
            selectedScale: 0,
        };
        this.handleButtonDown = this.handleButtonDown.bind(this);
    }

    public componentDidMount() {
        fetch(server.trial(this.trialName()))
            .then((result) => result.json())
            .then((json) => {
                this.setState({scales: Object.entries(json.entries).map((e) => e[0])});
            });
    }

    public render = () => this.state.scales ?
        this.gamepad(<div className={"container"} >
            <div className={"row"}>
                {this.state.scales.map((scale, idx) =>
                    <div key={idx} className={"col"}>
                        <Trial trialName={this.trialName()} scale={scale}
                               selected={this.state.selectedScale !== undefined ? this.state.selectedScale === idx : false}/>
                    </div>)}
            </div>
        </div>) : <span/>

    private trialName = () => this.props.match.params.name;

    private handleLeft = () => {
        this.setState({selectedScale: Math.max(0, this.state.selectedScale - 1)});
    }

    private handleRight = () => {
        this.setState({selectedScale: Math.min(this.state.scales.length - 1, this.state.selectedScale + 1)});
    }

    private handleButtonDown(buttonName: GPButton) {
        if (buttonName === "Y") {
            this.handleLeft();
        } else if  (buttonName === "X") {
            this.handleRight();
        }
    }

    private gamepad = (children: JSX.Element) =>
        <Gamepad onButtonDown={this.handleButtonDown}>{children}</Gamepad>

}
