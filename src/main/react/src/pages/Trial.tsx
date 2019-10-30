import React, {Component} from "react";
import Gamepad, {Button as GPButton} from "react-gamepad";
import Button from "../components/Button";
import {server} from "../utils/server";
import {Intensity, Scale} from "./Scale";
import "./Trial.css";

interface Props {
    trialName: string;
    scale: string;
    selected: boolean;
}

interface State {
    scale: Scale;
    selectedIndex?: number;
}

interface Event {
    scale: string;
    intensity: number;
}

export class Trial extends Component<Props, State> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.handleButtonDown = this.handleButtonDown.bind(this);
    }

    public componentDidMount() {
        this.fetchScale(this.props.scale);
    }

    public render() {
        const buttons: JSX.Element = this.state ? this.buttons(this.state.scale) : <span/>;
        return this.gamepad(buttons);
    }

    private trialName = () => this.props.trialName;

    private fetchScale(name: string) {
        fetch(server.scale(name))
            .then((result) => result.json())
            .then((json) => this.setState({scale: json}));
    }

    private event(intensity: Intensity): Event {
        return {
            scale: this.state.scale.name,
            intensity: intensity.number,
        };
    }

    private logStateChange(intensity: Intensity) {
        fetch(server.trial(this.trialName()), {method: "PATCH", body: JSON.stringify(this.event(intensity))});
    }

    private buttons(scale: Scale) {
        const maybeSelected = this.props.selected ? "selected" : "";
        return scale ? <div className={"container trial-container " + maybeSelected}>
            <div className="row col justify-content-center"><h1 className="trial">{scale.description}</h1></div>
            {scale.intensities.map((intensity, index) =>
                <div key={index} className="row align-items-center">
                    <div className="col"/>
                    <Button intensity={intensity.number}
                            selected={this.state.selectedIndex !== undefined ? this.state.selectedIndex === index : false}/>
                    <div className="col">
                        <div className={"borg-button-label"}>{intensity.label}</div>
                    </div>
                    <div className="col"/>
                </div>)
            }
        </div> : <div/>;
    }

    private handleButtonDown(buttonName: GPButton) {
        const selectedIndex = this.state.selectedIndex === undefined ? 0 :
            buttonName === "A" ? Math.max(0, this.state.selectedIndex - 1) :
                buttonName === "B" ? Math.min(this.state.scale.intensities.length - 1, (this.state.selectedIndex + 1)) :
                    this.state.selectedIndex;

        if (selectedIndex !== undefined && this.state.selectedIndex !== selectedIndex) {
            this.logStateChange(this.state.scale.intensities[selectedIndex]);
        }
        this.setState({selectedIndex});
    }

    private gamepad = (children: JSX.Element) =>
        (this.props.selected) ?
            <Gamepad onButtonDown={this.handleButtonDown}>{children}</Gamepad> :
            <div>{children}</div>
}
